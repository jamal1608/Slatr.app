#!/usr/bin/env python3
"""
Quiz Generator - Creates multiple-choice quizzes from lecture notes.
Follows strict formatting rules for educational content.
"""

import re
import sys
import random
from pathlib import Path


def extract_topics(notes: str) -> list[dict]:
    """Extract topics and key concepts from lecture notes."""
    topics = []
    lines = notes.strip().split("\n")
    current_topic = {"title": "General", "content": []}

    for line in lines:
        stripped = line.strip()
        if not stripped:
            continue

        # Detect section headers (lines ending with : or starting with #)
        if stripped.endswith(":") or stripped.startswith("#"):
            if current_topic["content"]:
                topics.append(current_topic)
            title = stripped.rstrip(":").lstrip("#").strip()
            current_topic = {"title": title, "content": []}
        else:
            current_topic["content"].append(stripped)

    if current_topic["content"]:
        topics.append(current_topic)

    return topics


def find_formulas(text: str) -> list[str]:
    """Extract formulas from text (patterns with = and common math symbols)."""
    formula_patterns = [
        r'[A-Za-z]\s*=\s*[^,;]+',  # X = something
        r'[A-Za-z]\s*\+\s*[A-Za-z]\s*=',  # X + Y =
    ]
    formulas = []
    for pattern in formula_patterns:
        matches = re.findall(pattern, text)
        formulas.extend(matches)
    return formulas


def find_definitions(text: str) -> list[tuple[str, str]]:
    """Extract definitions (X is Y, X means Y, X refers to Y)."""
    patterns = [
        r'([A-Z][a-zA-Z\s]+)\s+is\s+(?:a|an|the)\s+(.+?)(?:\.|$)',
        r'([A-Z][a-zA-Z\s]+)\s+refers to\s+(.+?)(?:\.|$)',
        r'([A-Z][a-zA-Z\s]+)\s+means\s+(.+?)(?:\.|$)',
    ]
    definitions = []
    for pattern in patterns:
        matches = re.findall(pattern, text, re.MULTILINE)
        definitions.extend(matches)
    return definitions


def find_key_terms(text: str) -> list[str]:
    """Extract bold or capitalized key terms."""
    terms = []
    # Find **bold** terms
    bold_matches = re.findall(r'\*\*([^*]+)\*\*', text)
    terms.extend(bold_matches)
    # Find terms in parentheses
    paren_matches = re.findall(r'\(([^)]+)\)', text)
    terms.extend([t for t in paren_matches if len(t) < 50])
    return terms


def generate_questions_from_notes(notes: str, num_questions: int = 10) -> list[dict]:
    """Generate quiz questions from lecture notes."""
    topics = extract_topics(notes)
    questions = []

    all_content = " ".join(
        " ".join(t["content"]) for t in topics
    )

    formulas = find_formulas(all_content)
    definitions = find_definitions(all_content)
    key_terms = find_key_terms(all_content)

    # Generate questions from different sources
    question_generators = [
        _gen_formula_questions(formulas, topics),
        _gen_definition_questions(definitions, topics),
        _gen_term_questions(key_terms, topics),
        _gen_factual_questions(topics),
    ]

    for gen in question_generators:
        for q in gen:
            if len(questions) < num_questions:
                questions.append(q)

    # Pad with general questions if needed
    while len(questions) < num_questions:
        q = _gen_general_question(topics)
        if q:
            questions.append(q)

    return questions[:num_questions]


def _gen_formula_questions(formulas, topics):
    """Generate questions about formulas."""
    questions = []
    for formula in formulas[:3]:
        parts = formula.split("=")
        if len(parts) == 2:
            var = parts[0].strip()
            expr = parts[1].strip()
            q = {
                "question": f"In the formula {formula.strip()}, what does '{var}' represent?",
                "options": [
                    "The dependent variable",
                    "The independent variable",
                    "A constant value",
                    "An error term"
                ],
                "answer": "A",
                "explanation": f"In the formula {formula.strip()}, {var} is the dependent variable that depends on other quantities."
            }
            questions.append(q)
    return questions


def _gen_definition_questions(definitions, topics):
    """Generate questions from definitions."""
    questions = []
    for term, defn in definitions[:3]:
        q = {
            "question": f"Which of the following best describes '{term.strip()}'?",
            "options": [
                defn.strip(),
                "A type of measurement unit",
                "A mathematical operator",
                "A physical constant"
            ],
            "answer": "A",
            "explanation": f"According to the lecture notes, {term.strip()} is defined as {defn.strip()}."
        }
        # Shuffle to randomize correct answer position
        correct = q["options"][0]
        random.shuffle(q["options"])
        q["answer"] = chr(65 + q["options"].index(correct))
        questions.append(q)
    return questions


def _gen_term_questions(key_terms, topics):
    """Generate questions about key terms."""
    questions = []
    for term in key_terms[:3]:
        if len(term) > 3 and len(term) < 40:
            q = {
                "question": f"What is '{term.strip()}'?",
                "options": [
                    "A concept discussed in the lecture",
                    "A type of equation",
                    "A unit of measurement",
                    "A laboratory instrument"
                ],
                "answer": "A",
                "explanation": f"'{term.strip()}' is a key term mentioned in the lecture notes."
            }
            questions.append(q)
    return questions


def _gen_factual_questions(topics):
    """Generate factual recall questions."""
    questions = []
    for topic in topics[:3]:
        if topic["content"]:
            first_sentence = topic["content"][0][:100]
            q = {
                "question": f"The lecture section '{topic['title']}' primarily discusses:",
                "options": [
                    first_sentence,
                    "Historical background only",
                    "Mathematical proofs",
                    "Experimental results"
                ],
                "answer": "A",
                "explanation": f"The section '{topic['title']}' covers content related to {first_sentence}."
            }
            questions.append(q)
    return questions


def _gen_general_question(topics):
    """Generate a general comprehension question."""
    if not topics:
        return None

    topic = random.choice(topics)
    q = {
        "question": f"Which section of the lecture would you review to learn about '{topic['title']}'?",
        "options": [
            f"The section titled '{topic['title']}'",
            "The introduction section",
            "The conclusion section",
            "The bibliography section"
        ],
        "answer": "A",
        "explanation": f"The section '{topic['title']}' covers the relevant material."
    }
    return q


def format_quiz(questions: list[dict]) -> str:
    """Format questions into the required quiz template."""
    output = []
    for i, q in enumerate(questions, 1):
        block = f"""Q{i}: {q['question']}
A: {q['options'][0]}
B: {q['options'][1]}
C: {q['options'][2]}
D: {q['options'][3]}
ANSWER: {q['answer']}
EXPLANATION: {q['explanation']}
"""
        output.append(block)

    return "\n".join(output)


def main():
    """Main entry point."""
    print("=" * 60)
    print("  QUIZ GENERATOR - Lecture Notes to Multiple Choice")
    print("=" * 60)
    print()

    # Check for file argument
    if len(sys.argv) > 1:
        filepath = Path(sys.argv[1])
        if filepath.exists():
            notes = filepath.read_text(encoding="utf-8")
            print(f"[OK] Loaded notes from: {filepath.name}")
        else:
            print(f"[ERROR] File not found: {filepath}")
            sys.exit(1)
    else:
        print("Paste your lecture notes below.")
        print("When finished, press Enter twice (empty line) or use Ctrl+Z then Enter:")
        print("-" * 60)
        lines = []
        empty_count = 0
        try:
            while True:
                line = input()
                if line == "":
                    empty_count += 1
                    if empty_count >= 2:
                        break
                    lines.append(line)
                else:
                    empty_count = 0
                    lines.append(line)
        except EOFError:
            pass
        notes = "\n".join(lines)

    if not notes.strip():
        print("[ERROR] No notes provided.")
        sys.exit(1)

    print(f"[OK] Processing {len(notes)} characters of notes...")
    print()

    # Generate questions
    questions = generate_questions_from_notes(notes, num_questions=10)

    # Format output
    quiz_output = format_quiz(questions)

    # Display
    print("=" * 60)
    print("  GENERATED QUIZ")
    print("=" * 60)
    print()
    print(quiz_output)

    # Save to file
    output_path = Path("generated_quiz.txt")
    output_path.write_text(quiz_output, encoding="utf-8")
    print(f"[OK] Quiz saved to: {output_path.absolute()}")


if __name__ == "__main__":
    main()
